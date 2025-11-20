package biz.enter.dao;

import biz.enter.vo.EnterVO;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("enterDAO")
public class EnterDAO extends EgovAbstractMapper {
    public List<EnterVO> selectEnterList(int offset, int size)  throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("offset", offset);
        param.put("size", size);
        return selectList("EnterDAO.selectEnterList", param);
    }

    public int selectEnterCount() throws Exception {
        return selectOne("EnterDAO.selectEnterCount");
    }


    public void insertEnter(EnterVO enter) throws Exception {
        insert("EnterDAO.insertEnter",enter);
    }

    public EnterVO selectEnter(String enterId) throws Exception {
        return selectOne("EnterDAO.selectEnter", enterId);
    }

    public void updateEnter(EnterVO vo) throws Exception {
        update("EnterDAO.updateEnter", vo);
    }

    public void deleteEnter(String enterId) throws Exception {
        delete("EnterDAO.deleteEnter", enterId);
    }
}
