package com.example.demo.mapper;

import com.example.demo.model.db.TSystemUserPlanDBModel;
import lombok.ToString;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface TSystemUserPlanDBModelMapper {
    int deleteByPrimaryKey(Integer id);

    int truncateTable();

    int insert(TSystemUserPlanDBModel record);

    TSystemUserPlanDBModel selectByPrimaryKey(Integer id);

    TSystemUserPlanDBModel selectByCondition(@Param("name") String name, @Param("type") String type, @Param("date") Date date);

    TSystemUserPlanDBModel selectByNameAndDate(@Param("name") String name, @Param("date") Date date);

    List<TSystemUserPlanDBModel> list();
}