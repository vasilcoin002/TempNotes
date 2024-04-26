package org.example.tempnotes.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UpdateUserNotesOrderRequest {
    private List<String> newNotesIdList;
}
