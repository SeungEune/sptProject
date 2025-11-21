package biz.eqp_history.service;

import biz.eqp_history.dao.EqpHistoryDAO;
import biz.eqp_history.vo.EqpHistoryVO;
import biz.equipment.dao.EquipmentDAO;
import biz.equipment.dto.EquipmentRequest;
import biz.equipment.service.EquipmentService;
import biz.equipment.vo.Status;
import biz.user.dao.UserDAO;
import biz.user.service.impl.UserServiceImpl;
import biz.user.vo.UserVO;
import egovframework.EgovBootApplication;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
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

            equipmentService.insertEquipment(createEquipmentRequest(serialNumber, name));
            eqpIdArray[i] = equipmentDAO.selectBySerialNumber(serialNumber).getId();
        }
    }

    @Test
    public void 관리자_이력_등록을_성공한다() {
        eqpHistoryService.insertEqpHistoryVO(createEqpHistoryVO(0, 0));
        Assertions.assertThat(eqpHistoryDAO.getAllEqpHistory().size()).isEqualTo(1);
    }

    @Test
    public void 장비별_이력_조회를_성공한다() {
        eqpHistoryService.insertEqpHistoryVO(createEqpHistoryVO(0, 0));
        eqpHistoryService.insertEqpHistoryVO(createEqpHistoryVO(1, 0));
        eqpHistoryService.insertEqpHistoryVO(createEqpHistoryVO(2, 0));
        Assertions.assertThat(eqpHistoryDAO.getEqpHistoryByEqpId(eqpIdArray[0]).size()).isEqualTo(3);
    }

    private EqpHistoryVO createEqpHistoryVO(int i, int j) {
        return EqpHistoryVO.builder()
                .directorId(directorIdArray[i])
                .eqpId(eqpIdArray[j])
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

    private EquipmentRequest createEquipmentRequest(String serialNumber, String name) {
        return EquipmentRequest.builder()
                .serialNumber(serialNumber)
                .accessNumber("accessNumber")
                .director(name)
                .status(Status.USE)
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
