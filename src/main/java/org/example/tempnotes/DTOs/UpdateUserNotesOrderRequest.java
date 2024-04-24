package org.example.tempnotes.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

// TODO add checking if all the notes are the same as were but reordered
@Data
@AllArgsConstructor
public class UpdateUserNotesOrderRequest {
    private String userId;
    private List<String> newNotesIdList;
}
