package com.bajabob.foglight

enum class ProguardEvalResult {
    DO_NOTHING,

    // proguard detected, log please!
    INFRACTION_LOG,

    // proguard detected, make it known!
    INFRACTION_CRASH
}