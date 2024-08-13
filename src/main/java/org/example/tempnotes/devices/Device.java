package org.example.tempnotes.devices;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tempnotes.tokens.Token;
import org.example.tempnotes.users.User;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "devices")
public class Device {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    @OneToMany(mappedBy = "device")
    @Builder.Default
    private List<Token> tokens = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Override
    public String toString() {
        return "Device" +
                "(id=" + id.toString() +
                ", name='" + name + '\'' +
                ", status=" + status.toString() +
                ", userId=" + user.getId().toString() +
                ')';
    }
}