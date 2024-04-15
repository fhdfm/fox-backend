package com.example.demo.dto;

import java.util.UUID;

public class AddDisciplinaRequest {
    
    private UUID[] ids;

    public UUID[] getIds() {
        return ids;
    }

    public void setIds(UUID[] ids) {
        this.ids = ids;
    }

}
