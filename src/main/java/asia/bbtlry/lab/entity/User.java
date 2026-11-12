package asia.bbtlry.lab.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
// 这里要导入 MyBatis-Plus 的三个东西: TableName、TableId、IdType

@Data
@TableName("user")   // 告诉 MyBatis-Plus：这个类对应哪张表
public class User {

    @TableId(type = IdType.AUTO) // 主键；AUTO = 数据库自增，插入时不用给
    private Long id;

    private String username;

    private String password;

    private String realName;

    private String role;

    private Integer enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
