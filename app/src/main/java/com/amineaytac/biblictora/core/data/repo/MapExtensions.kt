package com.amineaytac.biblictora.core.data.repo

inline fun <I, O> I.mapTo(crossinline mapper: (I) -> O): O {
    return mapper(this)
}