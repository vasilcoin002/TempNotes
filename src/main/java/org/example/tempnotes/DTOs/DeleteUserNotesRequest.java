package org.example.tempnotes.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// TODO refactor it and UpdateUserNotesOrder to the one class ChangeUserNotesRequest
public class DeleteUserNotesRequest {
    private List<String> notesIdList;
}
