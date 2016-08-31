package com.hazelcast.hibernate;

import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cache.access.AccessType;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

public class ManageEmployee {
    public static SessionFactory clientSf = createClientSessionFactory(getCacheProperties());


    protected static String getCacheStrategy() {
        return AccessType.READ_WRITE.getName();
    }


    protected static Properties getCacheProperties() {
        Properties props = new Properties();
        props.setProperty(Environment.CACHE_REGION_FACTORY, HazelcastCacheRegionFactory.class.getName());
        return props;
    }

    protected static SessionFactory createClientSessionFactory(Properties props) {
        Configuration conf = new Configuration();
        URL xml = ManageEmployee.class.getClassLoader().getResource("test-hibernate-client.cfg.xml");
        conf.configure(xml);
        conf.setCacheConcurrencyStrategy(DummyEntity.class.getName(), getCacheStrategy());
        conf.setCacheConcurrencyStrategy(DummyProperty.class.getName(), getCacheStrategy());
        conf.setCollectionCacheConcurrencyStrategy(DummyEntity.class.getName() + ".properties", getCacheStrategy());
        conf.addProperties(props);
        final SessionFactory sf = conf.buildSessionFactory();
        sf.getStatistics().setStatisticsEnabled(true);
        return sf;
    }

    @SuppressWarnings({"checkstyle:cyclomaticcomplexity", "checkstyle:methodlength"})
    public static void main(String[] args) throws InterruptedException {


        Session session = clientSf.openSession();
        Transaction tx = session.beginTransaction();
        DummyEntity e = new DummyEntity((long) 1, "dummy:1", 123456d, new Date());
        session.save(e);
        tx.commit();
        session.close();

        session = clientSf.openSession();
        tx = session.beginTransaction();
        e = new DummyEntity((long) 2, "dummy:1", 123456d, new Date());
        session.save(e);
        tx.commit();
        session.close();

        session = clientSf.openSession();
        DummyEntity retrieved = (DummyEntity) session.get(DummyEntity.class, (long) 1);
//        assertEquals("dummy:1", retrieved.getName());
//
        session.close();
        session = clientSf.openSession();
        retrieved.setName("dummy:update");
        tx = session.beginTransaction();
        session.update(retrieved);
        tx.commit();
        session.close();

//        final SessionFactory factory;
//
//        Configuration conf = new Configuration();
//        URL xml = ManageEmployee.class.getClassLoader().getResource("hibernate.cfg.xml");
//        Properties props = new Properties();
//        props.setProperty(Environment.CACHE_REGION_FACTORY, HazelcastLocalCacheRegionFactory.class.getName());
//        props.put(CacheEnvironment.EXPLICIT_VERSION_CHECK, "true");
//        conf.configure(xml);
//
//        conf.setCacheConcurrencyStrategy(Employee.class.getName(), getCacheStrategy());
////        conf.setCacheConcurrencyStrategy(Employee.class.getName(), getCacheStrategy());
////        conf.setCollectionCacheConcurrencyStrategy(Employee.class.getName() + ".properties", getCacheStrategy());
//
//
//        try {
//            factory = conf.buildSessionFactory();
//            factory.getStatistics().setStatisticsEnabled(true);
//        } catch (Throwable ex) {
//            System.err.println("Failed to create sessionFactory object: " + ex.getMessage());
//            throw new ExceptionInInitializerError(ex);
//        }
//
//        new Thread(new Runnable() {
//            Random random = new Random();
//
//            @Override
//            public void run() {
//                while (true) {
//                    Session session1 = factory.openSession();
//                    Transaction tx = session1.beginTransaction();
//                    Employee e = (Employee) session1.get(Employee.class, 1);
//                    e.setFirstName(String.valueOf(random.nextInt()));
//                    session1.update(e);
//                    session1.evict(e);
//                    tx.commit();
//                    session1.close();
//                }
//            }
//        }).start();
//        new Thread(new Runnable() {
//            Random random = new Random();
//
//            @Override
//            public void run() {
//                while (true) {
//                    Session session1 = factory.openSession();
//                    Transaction tx = session1.beginTransaction();
//                    Employee e = (Employee) session1.get(Employee.class, 1);
//                    e.setFirstName(String.valueOf(random.nextInt()));
//
//                    session1.update(e);
//                    tx.commit();
//                    session1.close();
//                }
//            }
//        }).start();
//        System.out.println("started 2 threads");
//        Scanner reader = new Scanner(System.in);
//        Session session1 = factory.openSession();
//        Transaction tx1 = session1.beginTransaction();
//        Session session2 = factory.openSession();
//        Transaction tx2 = session2.beginTransaction();
//        Session currentSession = session1;
//        Transaction currentTx = tx1;
//        int current = 1;
//
//        while (true) {
//            Thread.sleep(100);
//            System.out.print("[" + current + ". session] Enter command: ");
//            String command = reader.nextLine();
//            if (command.equals("list")) {
//                int i = 1;
//                List employees = currentSession.createQuery("FROM Employee").list();
//                for (Object entry : employees) {
//                    Employee employee = (Employee) entry;
//                    Employee e = (Employee) currentSession.get(Employee.class, i++);
//                    System.out.print("Id: " + employee.getId());
//                    System.out.print(", first name: " + employee.getFirstName());
//                    System.out.print(", last name: " + employee.getLastName());
//                    System.out.println(", salary: " + employee.getSalary());
//                    if (e != null)
//                        currentSession.evict(e);
//                }
//            } else if (command.equals("add")) {
//                System.out.print("Id: ");
//                int id = reader.nextInt();
//                reader.nextLine();
//                System.out.print("First name: ");
//                String fname = reader.nextLine();
//                System.out.print("Last name: ");
//                String lname = reader.nextLine();
//                System.out.print("Salary: ");
//                int salary = reader.nextInt();
//                reader.nextLine();
//                Employee employee = new Employee(id, fname, lname, salary);
//                currentSession.save(employee);
//            } else if (command.equals("delete")) {
//                System.out.print("EmployeeID: ");
//                int employeeId = reader.nextInt();
//                reader.nextLine();
//                Employee employee;
//                employee = (Employee) currentSession.get(Employee.class, employeeId);
//                currentSession.delete(employee);
//            } else if (command.equals("close")) {
//                currentTx.commit();
//                currentSession.close();
//            } else if (command.equals("open")) {
//                if (current == 1) {
//                    session1 = factory.openSession();
//                    tx1 = session1.beginTransaction();
//                    currentSession = session1;
//                    currentTx = tx1;
//                } else {
//                    session2 = factory.openSession();
//                    tx2 = session2.beginTransaction();
//                    currentSession = session2;
//                    currentTx = tx2;
//                }
//            } else if (command.equals("help")) {
//                System.out.println("help         this menu");
//                System.out.println("list         list all employees");
//                System.out.println("add          add an employee");
//                System.out.println("delete       delete and employee");
//                System.out.println("open         open session and begin transaction");
//                System.out.println("close        commit transaction and close session");
//                System.out.println("change       change between two sessions");
//                System.out.println("exit         exit");
//            } else if (command.equals("exit")) {
//                if (!tx1.wasCommitted()) {
//                    tx1.commit();
//                    session1.close();
//                }
//                if (!tx2.wasCommitted()) {
//                    tx2.commit();
//                    session2.close();
//                }
//                factory.close();
//                break;
//            } else if (command.equals("change")) {
//                if (currentSession.equals(session1)) {
//                    currentSession = session2;
//                    currentTx = tx2;
//                    current = 2;
//                } else {
//                    currentSession = session1;
//                    currentTx = tx1;
//                    current = 1;
//                }
//            } else {
//                System.out.println("Command not found. Use help.");
//            }
//        }
    }
}
