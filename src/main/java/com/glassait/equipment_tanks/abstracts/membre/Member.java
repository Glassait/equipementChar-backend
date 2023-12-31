package com.glassait.equipment_tanks.abstracts.membre;

import com.glassait.equipment_tanks.model.membre.MemberModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Member {
    private String role;
    private int account_id;

    public Member(MemberModel memberModel) {
        this.role = memberModel.getRole();
        this.account_id = memberModel.getAccountId();
    }
}