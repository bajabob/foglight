package com.bajabob.foglight

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties

internal class ProguardEvaluator{

    private var isStarted = false
    private val exceptions = mutableMapOf<String, ProguardException>()
    private var default = ProguardException.CRASH_ON_ERROR

    fun addException(k: KClass<Any>, e: ProguardException) = addException(k, e, exceptions, default)

    fun setDefaultException(e: ProguardException) {
        if (isStarted) {
            throw IllegalStateException("Cannot change default permission after startup")
        }
        default = e
    }

    fun create() { isStarted = true }

    fun evaluate(data: Any): ProguardEvalResult {
        if (!isStarted) {
            throw IllegalStateException("Please invoke #create")
        }
        return evaluate(data, exceptions, default)
    }


    companion object {

        fun evaluate(data: Any, e: Map<String, ProguardException>, default: ProguardException): ProguardEvalResult {
            val isProguarded = containsProguardSignals(data)

            if (isProguarded) {

                val k = kClass(data)
                val name = getClassName(k)
                val state = e.getOrDefault(name, default)

                return when (state) {
                    ProguardException.CRASH_ON_ERROR -> ProguardEvalResult.INFRACTION_CRASH
                    ProguardException.LOG_ON_ERROR -> ProguardEvalResult.INFRACTION_LOG
                }
            }
            return ProguardEvalResult.DO_NOTHING
        }

        fun addException(k: KClass<Any>, e: ProguardException, settings: MutableMap<String, ProguardException>, default: ProguardException) {
            if (default == e) {
                throw IllegalArgumentException("Specifying ${e.name} is illegal, it is already the default behavior.")
            }
            settings.put(getClassName(k), e)
        }

        fun getClassName(k: KClass<Any>): String {
            val name = k.qualifiedName
            if (name == null) {
                throw NullPointerException("The class that was specified is null and cannot be uniquely identified.")
            }
            return name
        }

        fun containsProguardSignals(data: Any): Boolean {

            val signals = proguardNameVariants()
            val clazz = kClass(data)

            var totalMembers = 0
            var totalProguardSignals = 0

            clazz.declaredMemberProperties.forEach {
                totalMembers++
                if (signals.contains(it.name)) {
                    totalProguardSignals++
                }
            }

            if (totalMembers == 0) {
                return false
            } else {
                return totalMembers == totalProguardSignals
            }
        }

        fun kClass(data: Any): KClass<Any> {
            return data.javaClass.kotlin
        }

        fun proguardNameVariants(): List<String> {
            val alphabet = listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
                    "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")

            return alphabet
        }
    }

}