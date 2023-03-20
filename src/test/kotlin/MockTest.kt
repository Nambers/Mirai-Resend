package tech.eritquearcus

import net.mamoe.mirai.mock.MockBotFactory
import org.junit.jupiter.api.BeforeAll

class MockTest {
    companion object {
        @JvmStatic
        @BeforeAll
        fun init(): Unit {
            MockBotFactory.initialize()
            Resend.onEnable()
        }
    }
    // TODO
}