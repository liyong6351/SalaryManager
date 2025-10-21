package com.example.demo.mapper;

import com.example.demo.model.db.TSystemPlanDBModel;
import com.example.demo.model.db.TSystemUserPlanDBModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
public interface TSystemPlanDBModelMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByIdList(@Param("list") List<Integer> idList);

    int insert(TSystemPlanDBModel record);

    int insertBatch(@Param("list") List<TSystemPlanDBModel> list);

    int insertSelective(TSystemPlanDBModel record);

    TSystemPlanDBModel selectByPrimaryKey(Integer id);

    Integer selectByTypeAndDate(@Param("type") String type, @Param("date") Date date);

    List<String> selectValidType();

    int updateByPrimaryKeySelective(TSystemPlanDBModel record);

    int updateByPrimaryKey(TSystemPlanDBModel record);

    void truncateTable();

    List<TSystemPlanDBModel> list();
}