package me.carmelo.theforums.model.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.carmelo.theforums.model.enums.OperationStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationResult<T> {
    private OperationStatus status;
    private String message;
    private T data;
}