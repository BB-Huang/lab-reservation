package asia.bbtlry.lab.mapper;

import asia.bbtlry.lab.entity.Device;
import asia.bbtlry.lab.entity.Reservation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {

}