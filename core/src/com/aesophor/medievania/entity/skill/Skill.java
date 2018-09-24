package com.aesophor.medievania.entity.skill;

import com.aesophor.medievania.component.skill.SkillType;
import com.badlogic.ashley.core.Entity;

public class Skill extends Entity {

    private final SkillType type;

    public Skill() {
        type = null; // implement this later.
    }


    public SkillType getType() {
        return type;
    }

}