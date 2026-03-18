package com.project.OPDeckManager.domain.entities;

import jakarta.validation.constraints.NotEmpty;

public class Leader extends Card{

    @NotEmpty
    public String life;
}
