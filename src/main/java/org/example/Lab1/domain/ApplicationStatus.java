package org.example.Lab1.domain;

public enum ApplicationStatus {
    WISHLIST,
    APPLIED,
    INTERVIEW,
    OFFER,
    REJECTED,
    WITHDRAWN;

    public boolean canMoveTo(ApplicationStatus next){
        return switch (this) {
            case WISHLIST -> next == APPLIED || next == WITHDRAWN;
            case APPLIED   -> next == INTERVIEW || next == REJECTED || next == WITHDRAWN;
            case INTERVIEW -> next == OFFER || next == REJECTED || next == WITHDRAWN;
            default        -> false;
        };
    }
}
