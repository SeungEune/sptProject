package biz.eqp_history.service;

import biz.eqp_history.dao.EqpHistoryDAO;
import biz.eqp_history.vo.EqpHistoryVO;
import biz.equipment.dao.EquipmentDAO;
import biz.equipment.dto.EquipmentRequest;
import biz.equipment.dto.EquipmentUpdate;
import biz.equipment.service.EquipmentService;
import biz.equipment.vo.EquipmentVO;
import biz.equipment.vo.Status;
import biz.user.dao.UserDAO;
import biz.user.service.impl.UserServiceImpl;
import biz.user.vo.UserVO;
import egovframework.EgovBootApplication;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Slf4j
@SpringBootTest(classes = EgovBootApplication.class)
public class EqpHistoryServiceTest {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private EqpHistoryService eqpHistoryService;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private EquipmentDAO equipmentDAO;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private EqpHistoryDAO eqpHistoryDAO;

    String[] directorArray = {"김철수", "홍길동", "김영희"};
    String[] directorIdArray = new String[directorArray.length];
    Long[] eqpIdArray = new Long[directorArray.length];

    @BeforeEach
    public void init() throws Exception {

        for (int i = 0; i < directorArray.length; i++) {
            String name = directorArray[i];
            String email = emailCreator(i);
            String serialNumber = serialCreator(i);
            String userId = userIdCreator(name);

            userService.createUser(createUserVO(userId, email, name));
            directorIdArray[i] = userDAO.selectUserByEmail(email).getUserId();

            equipmentService.insertEquipment(createEquipmentRequest(userId, serialNumber, name));
            eqpIdArray[i] = equipmentDAO.selectBySerialNumber(serialNumber).getId();
        }
        log.info("--- History size after init(): {} ---", eqpHistoryDAO.getAllEqpHistory().size());
    }

    @Test
    public void 관리자_이력_등록을_성공한다() {
        eqpHistoryService.insertEqpHistoryVO(createEqpHistoryVO(0, 0));
        Assertions.assertThat(eqpHistoryDAO.getAllEqpHistory().size()).isEqualTo(4);
    }

    @Test
    public void 장비별_이력_조회를_성공한다() {

        eqpHistoryService.insertEqpHistoryVO(createEqpHistoryVO(0, 0)); // 장비 0
        eqpHistoryService.insertEqpHistoryVO(createEqpHistoryVO(1, 0)); // 장비 0
        eqpHistoryService.insertEqpHistoryVO(createEqpHistoryVO(2, 0)); // 장비 0

        Assertions.assertThat(eqpHistoryDAO.getEqpHistoryByEqpId(eqpIdArray[0]).size()).isEqualTo(4);
    }

    @Test
    public void 수정전_관리자와_수정후_관리자가_동일하면_이력이_생성되지_않는다() {
        equipmentService.updateEquipment(
                createUpdate(eqpIdArray[0], "시리얼넘버", "엑세스넘버", directorIdArray[0], directorArray[0], Status.USE));

        Assertions.assertThat(eqpHistoryDAO.getAllEqpHistory().size()).isEqualTo(3);
    }

    private EqpHistoryVO createEqpHistoryVO(int i, int j) {
        return EqpHistoryVO.builder()
                .eqpId(eqpIdArray[j])
                .directorId(directorIdArray[i])
                .build();
    }

    private UserVO createUserVO(String userId, String email, String name) {
        return UserVO.builder()
                .userId(userId)
                .email(email)
                .jssfcCd("jssfcCd")
                .name(name)
                .registerId("1234")
                .password("1234")
                .passwordChk("1234")
                .build();
    }

    private EquipmentRequest createEquipmentRequest(String directorId, String serialNumber, String name) {
        return EquipmentRequest.builder()
                .serialNumber(serialNumber)
                .accessNumber("accessNumber")
                .directorId(directorId)
                .director(name)
                .status(Status.USE)
                .build();
    }

    private EquipmentVO createEquipmentVO(String serialNumber, String accessNumber, String directorId, String director, Status status) {
        return new EquipmentVO().create(createRequest(serialNumber, accessNumber, directorId, director, status));
    }

    private EquipmentRequest createRequest(String serialNumber, String accessNumber, String directorId, String director, Status status) {
        return EquipmentRequest.builder()
                .serialNumber(serialNumber)
                .accessNumber(accessNumber)
                .directorId(directorId)
                .director(director)
                .status(status)
                .build();
    }

    private EquipmentUpdate createUpdate(Long id, String serialNumber, String accessNumber, String directorId, String director, Status status) {
        return EquipmentUpdate.builder()
                .id(id)
                .serialNumber(serialNumber)
                .accessNumber(accessNumber)
                .directorId(directorId)
                .director(director)
                .status(status)
                .build();
    }

    private String emailCreator(int i) {
        return "testemail" + i + "@spt.com";
    }

    private String serialCreator(int i) {
        return "serial" + i;
    }

    private String userIdCreator(String name) {
        return name + "Id123";
    }
}