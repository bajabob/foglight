package com.bajabob.foglight

class ProguardDetectedException : RuntimeException {

    val classname: String

    constructor(data: Any) : super() {
        classname = ProguardEvaluator.getClassName(ProguardEvaluator.kClass(data))
    }

    override fun getLocalizedMessage(): String {
        return "$classname ${super.getLocalizedMessage()}"
    }
}