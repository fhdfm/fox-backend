package com.example.demo.dto;

import java.util.List;
import java.util.UUID;

public class AddDisciplinaRequest {
    
    private List<UUID> ids;

    public List<UUID> getIds() {
        return ids;
    }

    public void setIds(List<UUID> ids) {
        this.ids = ids;
    }

}
