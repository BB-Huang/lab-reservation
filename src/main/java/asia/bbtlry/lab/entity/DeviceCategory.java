package asia.bbtlry.lab.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
// 这里要导入 MyBatis-Plus 的三个东西: TableName、TableId、IdType

@Data
@TableName("device_category")   // 告诉 MyBatis-Plus：这个类对应哪张表
public class DeviceCategory {

    @TableId(type = IdType.AUTO) // 主键；AUTO = 数据库自增，插入时不用给
    private Long id;

    private String name;

    private Integer sort;
}