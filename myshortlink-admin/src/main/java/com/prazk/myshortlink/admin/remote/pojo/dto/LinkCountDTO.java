package com.prazk.myshortlink.admin.remote.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkCountDTO {
    private List<String> gid;
}
