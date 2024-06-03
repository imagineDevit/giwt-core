package io.github.imagineDevit.giwt.core;

/**
 * A record that represents a list of arguments
 * @author Henri Joel SEDJAME
 * @version 0.0.9
 */
@SuppressWarnings("unused")
public sealed interface Args {

    record Args2<T1, T2>(T1 arg1, T2 arg2) implements Args {
    }

    record Args3<T1, T2, T3>(T1 arg1, T2 arg2, T3 arg3) implements Args {
    }

    record Args4<T1, T2, T3, T4>(T1 arg1, T2 arg2, T3 arg3, T4 arg4) implements Args {
    }

    record Args5<T1, T2, T3, T4, T5>(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5) implements Args {
    }

    record Args6<T1, T2, T3, T4, T5, T6>(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6) implements Args {
    }

    record Args7<T1, T2, T3, T4, T5, T6, T7>(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6,
                                             T7 arg7) implements Args {
    }

    record Args8<T1, T2, T3, T4, T5, T6, T7, T8>(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7,
                                                 T8 arg8) implements Args {
    }

    record Args9<T1, T2, T3, T4, T5, T6, T7, T8, T9>(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7,
                                                     T8 arg8, T9 arg9) implements Args {
    }

    record Args10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6,
                                                           T7 arg7, T8 arg8, T9 arg9, T10 arg10) implements Args {
    }
}
