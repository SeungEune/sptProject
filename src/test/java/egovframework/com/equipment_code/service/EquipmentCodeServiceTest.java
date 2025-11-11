package egovframework.com.equipment_code.service;

import egovframework.com.equipment.dao.EquipmentDAO;
import egovframework.com.equipment_code.dao.EquipmentCodeDAO;
import egovframework.com.equipment_code.dto.EquipmentCodeRequest;
import egovframework.com.equipment_code.dto.EquipmentCodeResponse;
import egovframework.com.equipment_code.dto.EquipmentCodeUpdate;
import egovframework.com.equipment_code.vo.EquipmentCodeVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
public class EquipmentCodeServiceTest {

    @Autowired
    private EquipmentCodeDAO equipmentCodeDAO;

    @Autowired
    private EquipmentDAO equipmentDAO;

    @Autowired
    private EquipmentCodeService equipmentCodeService;

    EquipmentCodeVO testVO;

    @BeforeEach
    public void setUp() throws Exception {
        equipmentDAO.deleteAll();
        equipmentCodeDAO.deleteAll();
        testVO= createEquipmentCodeVO("AAAAA", "모니터");
        equipmentCodeDAO.save(testVO);

        equipmentCodeDAO.save(createEquipmentCodeVO("BBBBB", "키보드"));
    }

    @Test
    void 장비_분류_전체_조회를_성공한다() {
        assertThat(equipmentCodeService.getCodes().size()).isEqualTo(2);
    }

    @Test
    void 장비_분류_등록을_성공한다() {
        equipmentCodeService.insertCode(createRequest("CCCCCC", "마우스"));
        assertThat(equipmentCodeDAO.findAll().size()).isEqualTo(3);
    }

    @Test
    void 장비_분류_수정을_성공한다(){
        //given
        equipmentCodeService.updateCode(createUpdate(testVO.getId(), "AAAAAA","아이스크림"));
        assertThat(equipmentCodeDAO.findById(testVO.getId()).getName()).isEqualTo("아이스크림");
    }

    @Test
    void 장비_분류_삭제를_성공한다(){
        equipmentCodeService.deleteCode(testVO.getId());
        assertThat(equipmentCodeDAO.findAll().size()).isEqualTo(1);
    }

    @Test
    void 장비_분류_필터_조회를_성공한다(){
        List<String> names = new ArrayList<String>();
        names.add("모니터");
        names.add("키보드");
        assertThat(equipmentCodeService.getFilteredCode(names).size()).isEqualTo(2);
//        List<EquipmentCodeResponse> responses= equipmentCodeService.getFilteredCode(names);
    }

    private EquipmentCodeVO createEquipmentCodeVO(String code, String name) {
        return new EquipmentCodeVO().create(createRequest(code, name));
    }

    private EquipmentCodeRequest createRequest(String code, String name) {
        return EquipmentCodeRequest.builder()
                .code(code)
                .name(name)
                .build();
    }

    private EquipmentCodeUpdate createUpdate(Long id, String code, String name) {
        return EquipmentCodeUpdate.builder()
                .id(id)
                .code(code)
                .name(name)
                .build();
    }



}
