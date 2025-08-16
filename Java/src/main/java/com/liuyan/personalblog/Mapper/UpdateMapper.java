package com.liuyan.personalblog.Mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UpdateMapper {

    @Insert("insert into updates(update_date,content) values(now(),#{content})")
    void InsertUpdate(String content);
}
