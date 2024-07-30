package com.example.cashcard

import com.example.cashcard.models.CashCard
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester
import java.io.IOException


@JsonTest
internal class CashCardJsonTest {
    @Autowired
    private val json: JacksonTester<CashCard>? = null

    @Test
    @Throws(IOException::class)
    fun cashCardSerializationTest() {
        val cashCard = CashCard(99L, 123.45)
        Assertions.assertThat(json!!.write(cashCard)).isStrictlyEqualToJson("expected.json")
        Assertions.assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.id")
        Assertions.assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.id")
            .isEqualTo(99)
        Assertions.assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.amount")
        Assertions.assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.amount")
            .isEqualTo(123.45)
    }

    @Test
    @Throws(IOException::class)
    fun cashCardDeserializationTest() {
        val expected = """
                {
                    "id": 99,
                    "amount": 123.45
                }
                """.trimIndent()
        Assertions.assertThat(json!!.parse(expected))
            .isEqualTo(CashCard(99L, 123.45))
        Assertions.assertThat(json.parseObject(expected).id).isEqualTo(99)
        Assertions.assertThat(json.parseObject(expected).amount).isEqualTo(123.45)
    }
}