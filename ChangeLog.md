# ChangeLog
* created a new class `Message` for exchanging requests and replies
* structured java files to ease management and understanding
* changed the port num used in `OverallTest` to avoid port num invalid exception
* changed the code of `RemoteException`, added more inherited constructor
# Notice
* the TODO in `RegistryStubInvocationHandler` has not been done since in my implementation stubs are stored in the registry, there is no need to specially handle `lookup()`
* the bonus TODO in LocateRegistry has been done, modified the corresponding method and the constructor of `RegistryStubInvocationHandler`
* compared to the code in assignment1, the code in `my_server_client` only changed imports and deleted loggers.