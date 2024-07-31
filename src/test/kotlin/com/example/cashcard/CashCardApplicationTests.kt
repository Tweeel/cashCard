package com.example.cashcard

import com.example.cashcard.models.CashCard
import com.jayway.jsonpath.JsonPath
import net.minidev.json.JSONArray
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import java.net.URI


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
internal class CashCardApplicationTests {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun shouldNotReturnACashCardWhenUsingBadCredentials() {
        var response = restTemplate
            .withBasicAuth("BAD-USER", "abc123")
            .getForEntity("/cashcards/99", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)

        response = restTemplate
            .withBasicAuth("sarah1", "BAD-PASSWORD")
            .getForEntity("/cashcards/99", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun shouldRejectUsersWhoAreNotCardOwners() {
        val response = restTemplate
            .withBasicAuth("hank-owns-no-cards", "qrs456")
            .getForEntity("/cashcards/99", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun shouldNotAllowAccessToCashCardsTheyDoNotOwn() {
        val response = restTemplate
            .withBasicAuth("sarah1", "abc123")
            .getForEntity("/cashcards/102", String::class.java) // kumar2's data
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun shouldReturnAllCashCardsWhenListIsRequested() {
        val response = restTemplate.withBasicAuth("sarah1", "abc123") // Add this
            .getForEntity("/cashcards", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val documentContext = JsonPath.parse(response.body)
        val cashCardCount = documentContext.read<Int>("$.length()")

        assertThat(cashCardCount).isEqualTo(3)

        val ids: JSONArray = documentContext.read("$..id")
        assertThat(ids).containsExactlyInAnyOrder(99, 100, 101)

        val amounts: JSONArray = documentContext.read("$..amount")
        assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.0, 150.00)
    }

    @Test
    fun shouldReturnAPageOfCashCards() {
        val response = restTemplate.withBasicAuth("sarah1", "abc123") // Add this
            .getForEntity(
                "/cashcards?page=0&size=1",
                String::class.java
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val documentContext = JsonPath.parse(response.body)
        val page = documentContext.read<JSONArray>("$[*]")
        assertThat(page.size).isEqualTo(1)
    }

    @Test
    fun shouldReturnASortedPageOfCashCards() {
        val response = restTemplate.withBasicAuth("sarah1", "abc123") // Add this
            .getForEntity(
                "/cashcards?page=0&size=1&sort=amount,asc",
                String::class.java
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val documentContext = JsonPath.parse(response.body)
        val read = documentContext.read<JSONArray>("$[*]")
        assertThat(read.size).isEqualTo(1)

        val amount = documentContext.read<Double>("$[0].amount")
        assertThat(amount).isEqualTo(1.00)
    }

    @Test
    fun shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {
        val response = restTemplate.withBasicAuth("sarah1", "abc123") // Add this
            .getForEntity("/cashcards", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val documentContext = JsonPath.parse(response.body)
        val page = documentContext.read<JSONArray>("$[*]")
        assertThat(page.size).isEqualTo(3)

        val amounts = documentContext.read<JSONArray>("$..amount")
        assertThat(amounts).containsExactly(1.00, 123.45, 150.00)
    }

    @Test
    fun shouldReturnACashCardWhenDataIsSaved() {
        val response = restTemplate
            .withBasicAuth("sarah1", "abc123") // Add this
            .getForEntity("/cashcards/99", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val documentContext = JsonPath.parse(response.body)
        val id = documentContext.read<Number>("$.id")
        assertThat(id).isEqualTo(99)

        val amount = documentContext.read<Double>("$.amount")
        assertThat(amount).isEqualTo(123.45)
    }

    @Test
    @DirtiesContext
    fun shouldCreateANewCashCard() {
        val newCashCard = CashCard(null, 250.00, null)
        val createResponse = restTemplate.withBasicAuth("sarah1", "abc123") // Add this
            .postForEntity(
                "/cashcards", newCashCard,
                Void::class.java
            )
        assertThat(createResponse.statusCode).isEqualTo(HttpStatus.CREATED)

        val locationOfNewCashCard: URI? = createResponse.headers.location
        val getResponse = restTemplate.withBasicAuth("sarah1", "abc123") // Add this
            .getForEntity(locationOfNewCashCard, String::class.java)
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)
        // Add assertions such as these
        val documentContext = JsonPath.parse(getResponse.body)
        val id = documentContext.read<Number>("$.id")
        val amount = documentContext.read<Double>("$.amount")

        assertThat(id).isNotNull()
        assertThat(amount).isEqualTo(250.00)
    }
}
