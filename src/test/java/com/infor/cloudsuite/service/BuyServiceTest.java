package com.infor.cloudsuite.service;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dao.ConsultRequestDao;
import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dto.BuyConsultDto;
import com.infor.cloudsuite.entity.ConsultRequest;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.components.NullEmailProvider;

/**
 * User: bcrow
 * Date: 10/28/11 10:08 AM
 */
@Transactional(propagation = Propagation.REQUIRED)
public class BuyServiceTest extends AbstractTest{

    @Resource
    private BuyService buyService;
    @Resource
    private SeedService seedService;
    @Resource
    private ProductDao productDao;
    @Resource
    private ConsultRequestDao consultRequestDao;
    @Resource
    private UserDao userDao; 

    @Test
    public void testRequestConsult() throws Exception {
        BuyConsultDto dto = new BuyConsultDto();
        dto.setProductId(productDao.findByShortName("XM").getId());
        dto.setEmail("test@test.com");
        dto.setPhone("555-555-5555");
        loginTestUser();
        final NullEmailProvider emailProvider = new NullEmailProvider();
        buyService.setEmailProvider(emailProvider);
        BuyConsultDto bcDto = buyService.requestConsult(dto);
        assertNotNull(bcDto);
        assertEquals(dto.getProductId(), bcDto.getProductId());
        assertEquals(dto.getEmail(), bcDto.getEmail());
        assertEquals(dto.getPhone(), dto.getPhone());
        final User user = userDao.findByUsername(testUserName);
        final ConsultRequest request = consultRequestDao.findByUserId(user.getId());
        assertEquals(user.getId(), request.getUser().getId());
        assertEquals(dto.getProductId(), request.getProduct().getId());
        assertEquals(dto.getEmail(), request.getEmail());
        assertEquals(dto.getPhone(), request.getPhone());
        assertEquals("Both emails were sent.", 2, emailProvider.getAsyncEmails().size());
    }

    @Test
    public void testGeneralInformation() throws Exception {
        BuyConsultDto dto = new BuyConsultDto();
        dto.setProductId(-1L);
        dto.setEmail("test@test.com");
        dto.setPhone("555-555-5555");
        loginTestUser();

        final NullEmailProvider emailProvider = new NullEmailProvider();
        buyService.setEmailProvider(emailProvider);
        BuyConsultDto bcDto = buyService.requestConsult(dto);
        assertNotNull(bcDto);
        assertEquals(new Long(-1L), bcDto.getProductId());
        assertEquals(dto.getEmail(), bcDto.getEmail());
        assertEquals(dto.getPhone(), bcDto.getPhone());
        final User user = userDao.findByUsername(testUserName);
        final ConsultRequest request = consultRequestDao.findByUserId(user.getId());
        assertEquals(user.getId(), request.getUser().getId());
        assertNull(request.getProduct());
        assertEquals(dto.getEmail(), request.getEmail());
        assertEquals(dto.getPhone(), request.getPhone());
        assertEquals("Both Emails were sent.", 2, emailProvider.getAsyncEmails().size());
    }
}
