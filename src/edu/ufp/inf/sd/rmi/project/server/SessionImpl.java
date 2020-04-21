package edu.ufp.inf.sd.rmi.project.server;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class SessionImpl implements SessionRI {
    DBMockup db;
    public SessionImpl(DBMockup db) {
        this.db = db;
    }

    @Override
    public ArrayList listTaskGroups() throws RemoteException {
        return null;
    }

    @Override
    public SubjectRI createTaskGroup(String name, String hash) throws RemoteException {
        return null;
    }

    @Override
    public boolean pauseTaskGroup(SubjectRI subjectRI) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteTaskGroup(SubjectRI subjectRI) throws RemoteException {
        return false;
    }


    @Override
    public void logout() throws RemoteException {

    }
}
