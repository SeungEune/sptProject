package egovframework.com.equipment.service;

import biz.equipment.dao.EquipmentDAO;
import biz.equipment.dto.EquipmentRequest;
import biz.equipment.dto.EquipmentUpdate;
import biz.equipment.service.EquipmentService;
import biz.equipment.vo.EquipmentVO;
import biz.equipment.vo.Status;
import egovframework.com.config.TestMapperConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;


import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Import(TestMapperConfig.class)
public class EquipmentServiceTest {

    @Autowired
    private EquipmentDAO equipmentDAO;

    @Autowired
    private EquipmentService equipmentService;

    EquipmentVO testVO;

    @BeforeEach
    public void setUp() throws Exception {
        equipmentDAO.deleteAll();
        testVO = createEquipmentVO("AAAAA", "모니터","serialNumber1","accessNumber1", "홍길동", Status.STORAGE);
        equipmentDAO.save(testVO);

        equipmentDAO.save(createEquipmentVO("BBBBB", "키보드","serialNumber2","accessNumber2", "김철수", Status.USE));
    }

    @Test
    void 장비_전체_조회를_성공한다() {
        assertThat(equipmentService.getEquipments().size()).isEqualTo(2);
    }

    @Test
    void 장비_등록을_성공한다() {
        equipmentService.insertEquipment(createRequest("CCCCCC", "마우스", "serialNumber3", "accessNumber3", "김영희", Status.REPAIR));
        assertThat(equipmentDAO.findAll().size()).isEqualTo(3);
    }

    @Test
    void 장비_수정을_성공한다() {
        //given
        equipmentService.updateEquipment(
                createUpdate(testVO.getId(), "serialNumber5", "accessNumber5","홍길동", Status.USE));

        assertThat(equipmentDAO.findById(testVO.getId()).getSerialNumber()).isEqualTo("serialNumber5");
    }

    @Test
    void 장비_삭제를_성공한다() {
        equipmentService.deleteEquipment(testVO.getId());
        assertThat(equipmentDAO.findAll().size()).isEqualTo(1);
    }

    @Test
    void 관리자_수정을_성공한다(){
        equipmentService.updateDirector(testVO.getId(), "수정자");
        assertThat(equipmentDAO.findById(testVO.getId()).getDirector()).isEqualTo("수정자");
    }

    @Test
    void 장비_상태_변경을_성공한다(){
        equipmentService.updateStatus(testVO.getId(), Status.DISPOSAL);
        assertThat(equipmentDAO.findById(testVO.getId()).getStatus()).isEqualTo(Status.DISPOSAL);
    }

    private EquipmentVO createEquipmentVO(String code, String name, String serialNumber, String accessNumber, String director, Status status) {
        return new EquipmentVO().create(createRequest(code, name, serialNumber, accessNumber, director, status));
    }

    private EquipmentRequest createRequest(String code, String name, String serialNumber, String accessNumber, String director, Status status) {
        return EquipmentRequest.builder()
                .code(code)
                .name(name)
                .serialNumber(serialNumber)
                .accessNumber(accessNumber)
                .director(director)
                .status(status)
                .build();
    }

    private EquipmentUpdate createUpdate(Long id, String serialNumber, String accessNumber, String director, Status status) {
        return EquipmentUpdate.builder()
                .id(id)
                .serialNumber(serialNumber)
                .accessNumber(accessNumber)
                .director(director)
                .status(status)
                .build();
    }

}