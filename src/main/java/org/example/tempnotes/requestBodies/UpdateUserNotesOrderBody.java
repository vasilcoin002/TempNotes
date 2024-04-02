package org.example.tempnotes.requestBodies;

import lombok.Data;

import java.util.List;

@Data
public class UpdateUserNotesOrderBody {
    private String userId;
    private List<String> newNotesIdList;

    public UpdateUserNotesOrderBody(String userId, List<String> newNotesIdList) {
        this.userId = userId;
        this.newNotesIdList = newNotesIdList;
    }
}
