package com.example.cashcard

import com.jayway.jsonpath.JsonPath
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class CashCardApplicationTests {
    @Autowired
    var restTemplate: TestRestTemplate? = null

    @Test
    fun shouldReturnACashCardWhenDataIsSaved() {
        val response = restTemplate!!.getForEntity("/cashcards/99", String::class.java)

        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val documentContext = JsonPath.parse(response.body)
        val id = documentContext.read<Number>("$.id")
        Assertions.assertThat(id).isEqualTo(99)

        val amount = documentContext.read<Double>("$.amount")
        Assertions.assertThat(amount).isEqualTo(123.45)
    }
}
