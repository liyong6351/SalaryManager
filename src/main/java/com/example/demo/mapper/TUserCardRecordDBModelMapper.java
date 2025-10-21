package com.example.demo.mapper;

import com.example.demo.model.db.TUserCardRecordDBModel;
import com.example.demo.model.excel.DetailVo;
import com.example.demo.model.excel.StatisticsDetailVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface TUserCardRecordDBModelMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TUserCardRecordDBModel record);

    int insertSelective(TUserCardRecordDBModel record);

    TUserCardRecordDBModel selectByPrimaryKey(Integer id);

    TUserCardRecordDBModel selectByCondition(@Param("name") String userName, @Param("date") Date date);

    List<DetailVo> select4Detail();

    List<StatisticsDetailVo> select4Statistics(@Param("list") List<String> typeList);

    int updateByPrimaryKeySelective(TUserCardRecordDBModel record);

    int updateByPrimaryKey(TUserCardRecordDBModel record);

    void insertBatch(@Param("list") List<TUserCardRecordDBModel> list);


    void truncateTable();
}