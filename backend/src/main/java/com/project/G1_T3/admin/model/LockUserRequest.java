package com.project.G1_T3.admin.model;

public class LockUserRequest {
    private String userId;
    private boolean isLocked;

    public LockUserRequest(String userId, boolean isLocked) {
        this.userId = userId;
        this.isLocked = isLocked;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public boolean isLocked() {
        return isLocked;
    }
    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    
}
