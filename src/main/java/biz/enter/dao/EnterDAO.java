package biz.enter.dao;

import biz.enter.vo.EnterVO;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("enterDAO")
public class EnterDAO extends EgovAbstractMapper {
    public List<EnterVO> selectEnterList(){
        return selectList("EnterDAO.selectEnterList");
    }

    public void insertEnter(EnterVO enter) {
        insert("EnterDAO.insertEnter",enter);
    }

    public EnterVO selectEnter(Long enterId) {
        return selectOne("EnterDAO.selectEnter", enterId);
    }

    public void updateEnter(EnterVO vo) {
        update("EnterDAO.updateEnter", vo);
    }

    public void deleteEnter(Long enterId) {
        delete("EnterDAO.deleteEnter", enterId);
    }
}
