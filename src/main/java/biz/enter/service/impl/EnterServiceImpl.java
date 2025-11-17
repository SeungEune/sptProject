package biz.enter.service.impl;

import biz.enter.dao.EnterDAO;
import biz.enter.service.EnterService;
import biz.enter.vo.EnterVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("enterService")
public class EnterServiceImpl implements EnterService {
    @Resource(name = "enterDAO")
    private EnterDAO enterDAO;

    @Override
    public List<EnterVO> getEnterList() {
        return enterDAO.selectEnterList();
    }

    @Override
    public void createEnter(EnterVO enter) {
        enter.setStatus("NORMAL");
        enterDAO.insertEnter(enter);
    }

    @Override
    public EnterVO getEnter(Long enterId) throws Exception {
        return enterDAO.selectEnter(enterId);
    }

    @Override
    public void updateEnter(EnterVO vo) throws Exception {
        enterDAO.updateEnter(vo);
    }

    @Override
    public void deleteEnter(Long enterId) throws Exception {
        enterDAO.deleteEnter(enterId);
    }
}
