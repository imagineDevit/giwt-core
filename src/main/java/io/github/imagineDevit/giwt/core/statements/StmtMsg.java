package io.github.imagineDevit.giwt.core.statements;

import io.github.imagineDevit.giwt.core.utils.TextUtils;

public record StmtMsg(String value) {

    public static StmtMsg given(String value) {
       return new StmtMsg(TextUtils.blue("GIVEN ") + value);
    }

    public static StmtMsg when(String value) {
        return new StmtMsg( TextUtils.blue("WHEN ") + value);
    }

    public static StmtMsg then(String value) {
        return new StmtMsg(TextUtils.blue("THEN ") + value);
    }

    public static StmtMsg and(String value) {
        return new StmtMsg("↳ "+ TextUtils.blue("AND ") + value);
    }
}
