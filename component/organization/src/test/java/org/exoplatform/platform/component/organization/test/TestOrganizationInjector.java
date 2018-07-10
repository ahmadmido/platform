package org.exoplatform.platform.component.organization.test;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.platform.organization.injector.DataInjectorService;
import org.exoplatform.platform.organization.injector.JMXDataInjector;
import org.exoplatform.services.organization.*;
import org.junit.Before;
import org.junit.Test;
import org.picocontainer.Startable;

import java.io.File;

import static junit.framework.TestCase.assertNotNull;

public class TestOrganizationInjector {
  PortalContainer container = null;
  OrganizationService organizationService = null;
  JMXDataInjector dataInjector = null;
  DataInjectorService dataInjectorService = null;

  @Before
  public void setUp() throws Exception {
    container = PortalContainer.getInstance();
    organizationService = container.getComponentInstanceOfType(OrganizationService.class);
    dataInjector = container.getComponentInstanceOfType(JMXDataInjector.class);
    dataInjectorService = container.getComponentInstanceOfType(DataInjectorService.class);

    ((Startable) organizationService).start();
  }

  @Test
  public void testDataInjectorService() throws Exception {
    File file = new File("target/test.zip");
    dataInjector.extractData(file.getAbsolutePath());
    deleteMembershipType("dataMT");
    deleteGroup("dataGroup");
    deleteUser("dataUser1");
    deleteUser("dataUser2");
    dataInjector.injectData(file.getAbsolutePath());

    User user = getUser("dataUser1");
    assertNotNull(user);
    user = getUser("dataUser2");
    assertNotNull(user);

    Group group = getGroup("/dataGroup");
    assertNotNull(group);

    MembershipType membershipType = getMembershipType("dataMT");
    assertNotNull(membershipType);

    Membership membership = getMembership("dataMT", "dataUser1", "/dataGroup");
    assertNotNull(membership);

    membership = getMembership("dataMT", "dataUser2", "/dataGroup");
    assertNotNull(membership);
  }

  private Membership getMembership(String membershipTypeId, String userName, String groupId) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      return organizationService.getMembershipHandler().findMembershipByUserGroupAndType(userName, groupId, membershipTypeId);
    } finally {
      RequestLifeCycle.end();
    }
  }

  private MembershipType getMembershipType(String membershipTypeId) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      return organizationService.getMembershipTypeHandler().findMembershipType(membershipTypeId);
    } finally {
      RequestLifeCycle.end();
    }
  }

  private Group getGroup(String groupId) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      return organizationService.getGroupHandler().findGroupById(groupId);
    } finally {
      RequestLifeCycle.end();
    }
  }

  private User getUser(String userId) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      return organizationService.getUserHandler().findUserByName(userId);
    } finally {
      RequestLifeCycle.end();
    }
  }

  private void deleteMembershipType(String membershipTypeId) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      organizationService.getMembershipTypeHandler().removeMembershipType(membershipTypeId, false);
    } finally {
      RequestLifeCycle.end();
    }
  }

  private void deleteUser(String username) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      organizationService.getUserHandler().removeUser(username, false);
    } finally {
      RequestLifeCycle.end();
    }
  }

  private void deleteGroup(String groupId) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      organizationService.getGroupHandler().removeGroup(organizationService.getGroupHandler().findGroupById(groupId), false);
    } finally {
      RequestLifeCycle.end();
    }
  }

}
