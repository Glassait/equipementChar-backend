package com.glassait.equipment_tanks.abstracts.tank_data;

import com.glassait.equipment_tanks.model.tank.FieldsModel;
import lombok.Getter;

@Getter
public class Fields {
    private final int level;
    private final Field field;

    public Fields(FieldsModel fieldsModel) {
        this.level = fieldsModel.getLevel();
        this.field = new Field(fieldsModel);
    }
}
