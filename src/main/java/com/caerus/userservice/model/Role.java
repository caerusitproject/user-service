package com.caerus.userservice.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "roles")
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private String name;
  
}