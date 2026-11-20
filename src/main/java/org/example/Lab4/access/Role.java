package org.example.Lab4.access;

// Who is using the tracker. A VIEWER may only look at applications, an EDITOR may also change them.
public enum Role {
    VIEWER,
    EDITOR;

    public boolean canEdit() {
        return this == EDITOR;
    }
}
