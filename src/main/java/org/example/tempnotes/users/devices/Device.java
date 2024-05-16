package org.example.tempnotes.users.devices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Device {
    private String name;
    private DeviceStatus status;
}
