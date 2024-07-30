package com.example.cashcard

import com.example.cashcard.models.CashCard
import com.jayway.jsonpath.JsonPath
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import java.net.URI


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class CashCardApplicationTests {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun shouldReturnACashCardWhenDataIsSaved() {
        val response = restTemplate.getForEntity("/cashcards/99", String::class.java)

        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val documentContext = JsonPath.parse(response.body)
        val id = documentContext.read<Number>("$.id")
        Assertions.assertThat(id).isEqualTo(99)

        val amount = documentContext.read<Double>("$.amount")
        Assertions.assertThat(amount).isEqualTo(123.45)
    }

    @Test
    fun shouldCreateANewCashCard() {
        val newCashCard = CashCard(null, 250.00)
        val createResponse = restTemplate.postForEntity(
            "/cashcards", newCashCard,
            Void::class.java
        )
        Assertions.assertThat(createResponse.statusCode).isEqualTo(HttpStatus.CREATED)

        val locationOfNewCashCard: URI? = createResponse.headers.location
        val getResponse = restTemplate.getForEntity(locationOfNewCashCard, String::class.java)
        Assertions.assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)

        // Add assertions such as these
        val documentContext = JsonPath.parse(getResponse.body)
        val id = documentContext.read<Number>("$.id")
        val amount = documentContext.read<Double>("$.amount")

        Assertions.assertThat(id).isNotNull()
        Assertions.assertThat(amount).isEqualTo(250.00)
    }
}
