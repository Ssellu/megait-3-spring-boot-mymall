package com.megait.mymall.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("gm")
@Getter
@Setter
@NoArgsConstructor
public class Game extends Item {
    private String title;
    private String publisher;

}
