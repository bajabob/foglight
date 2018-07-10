import com.bajabob.foglight.ProguardException
import com.bajabob.foglight.ProguardEvaluator
import com.bajabob.foglight.ProguardEvalResult
import org.junit.Assert.*
import org.junit.Test

class ProguardEvaluatorTest {

    data class NotProguardedDataExample(val c: Boolean, val e: Int, val name: String)

    data class ProguardedDataExample(val a: String, val b: Boolean, val c: Int)

    data class RegularClass(val someName: String, val isSetup: Boolean, val range: Double, val nullable: Int?)



    @Test
    fun proguardNameVariants_ensureSize() {
        assertEquals(26, ProguardEvaluator.proguardNameVariants().size)
    }

    @Test
    fun kClass_UnitCheck() {
        assertEquals("Unit", ProguardEvaluator.kClass(Unit).simpleName)
    }

    @Test
    fun getClassName_unit() {
        val k = ProguardEvaluator.kClass(Unit)
        assertEquals("kotlin.Unit", ProguardEvaluator.getClassName(k))
    }

    @Test
    fun getClassName_proguardedDataExample() {
        val proguarded = ProguardedDataExample("hello", false, 42)

        val k = ProguardEvaluator.kClass(proguarded)

        assertEquals("ProguardEvaluatorTest.ProguardedDataExample",
                ProguardEvaluator.getClassName(k))
    }

    @Test
    fun evaluate_infractionCrash_noExceptions() {
        val default = ProguardException.CRASH_ON_ERROR
        val exceptions = mutableMapOf<String, ProguardException>()
        val proguarded = ProguardedDataExample("hello", false, 42)

        assertEquals(ProguardEvalResult.INFRACTION_CRASH, ProguardEvaluator.evaluate(proguarded, exceptions, default))
    }

    @Test
    fun evaluate_infractionLog_noExceptions() {
        val default = ProguardException.LOG_ON_ERROR
        val exceptions = mutableMapOf<String, ProguardException>()
        val proguarded = ProguardedDataExample("hello", false, 42)

        assertEquals(ProguardEvalResult.INFRACTION_LOG, ProguardEvaluator.evaluate(proguarded, exceptions, default))
    }

    @Test
    fun evaluate_infractionCrash_hasExceptions() {
        val default = ProguardException.LOG_ON_ERROR
        val exceptions = mutableMapOf<String, ProguardException>()
        exceptions.put("ProguardEvaluatorTest.ProguardedDataExample", ProguardException.CRASH_ON_ERROR)

        val proguarded = ProguardedDataExample("hello", false, 42)

        assertEquals(ProguardEvalResult.INFRACTION_CRASH, ProguardEvaluator.evaluate(proguarded, exceptions, default))
    }

    @Test
    fun evaluate_infractionLog_hasExceptions() {
        val default = ProguardException.CRASH_ON_ERROR
        val exceptions = mutableMapOf<String, ProguardException>()
        exceptions.put("ProguardEvaluatorTest.ProguardedDataExample", ProguardException.LOG_ON_ERROR)

        val proguarded = ProguardedDataExample("hello", false, 42)

        assertEquals(ProguardEvalResult.INFRACTION_LOG, ProguardEvaluator.evaluate(proguarded, exceptions, default))
    }

    @Test
    fun evaluate_doNothing_hasExceptions() {
        val default = ProguardException.CRASH_ON_ERROR
        val exceptions = mutableMapOf<String, ProguardException>()
        exceptions.put("ProguardEvaluatorTest.ProguardedDataExample", ProguardException.LOG_ON_ERROR)

        val normal = RegularClass("a", true, 1.1, null)

        assertEquals(ProguardEvalResult.DO_NOTHING, ProguardEvaluator.evaluate(normal, exceptions, default))
    }

    @Test
    fun addException_ensureAdded() {
        val exceptions = mutableMapOf<String, ProguardException>()
        val proguarded = ProguardedDataExample("hello", false, 42)
        val k = ProguardEvaluator.kClass(proguarded)

        ProguardEvaluator.addException(k, ProguardException.LOG_ON_ERROR, exceptions, ProguardException.CRASH_ON_ERROR)

        assertEquals(1, exceptions.size)
    }

    @Test
    fun addException_ensureCrash() {
        val exceptions = mutableMapOf<String, ProguardException>()
        val proguarded = ProguardedDataExample("hello", false, 42)
        val k = ProguardEvaluator.kClass(proguarded)

        try {
            ProguardEvaluator.addException(k, ProguardException.CRASH_ON_ERROR, exceptions, ProguardException.CRASH_ON_ERROR)
            fail("should not hit this line")
        } catch (e: IllegalArgumentException) { }
    }



    @Test
    fun containsProguardSignals_proguardedClass() {
        val proguarded = ProguardedDataExample("hello", false, 42)
        assertTrue(ProguardEvaluator.containsProguardSignals(proguarded))
    }

    @Test
    fun containsProguardSignals_notQuiteProguardedClass() {
        // close but not quite!
        val notProguarded = NotProguardedDataExample(false, 1, "hello")
        assertFalse(ProguardEvaluator.containsProguardSignals(notProguarded))
    }


    @Test
    fun containsProguardSignals_notProguardedClass() {
        val normal = RegularClass("a", true, 1.1, null)
        assertFalse(ProguardEvaluator.containsProguardSignals(normal))
    }
}