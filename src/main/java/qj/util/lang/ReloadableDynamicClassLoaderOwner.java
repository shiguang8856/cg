package qj.util.lang;

public class ReloadableDynamicClassLoaderOwner {
	ClassLoader parent;// both classloader's parent
	public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> c = null;
		c=this.getClass().getClassLoader().loadClass(name);
		if (null == c) {
			throw new ClassNotFoundException();
		}
		return c;
	}

	public static void main(String[] args) throws Exception {
		ReloadableDynamicClassLoaderOwner rdcl = new ReloadableDynamicClassLoaderOwner();
		rdcl.loadClass("test.CicularClass1", false);
		System.out.print("*************");
	}

    public Object getDirDynamicClassLoader() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getJarDynamicClassLoader() {
        // TODO Auto-generated method stub
        return null;
    }

}
