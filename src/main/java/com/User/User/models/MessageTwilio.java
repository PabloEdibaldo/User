package com.User.User.models;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_Message")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageTwilio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition="TEXT")
    private int caseMessage;
    private String messageTemplate;

}
