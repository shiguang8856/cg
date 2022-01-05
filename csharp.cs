using System;
using System.Diagnostics;
using System.Runtime.InteropServices;
using System.Threading;
using System.Windows.Forms;

namespace SendKeys
{
    static class Program
    {
        [DllImport("User32.dll")]
        static extern int SetForegroundWindow(IntPtr point);

        [DllImport("kernel32.dll")]
        static extern bool AttachConsole(int dwProcessId);

        private const int ATTACH_PARENT_PROCESS = -1;

        [DllImport("kernel32.dll", SetLastError = true)]
        [return: MarshalAs(UnmanagedType.Bool)]
        static extern bool FreeConsole();

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        public static int Main(string[] args)
        {
            if (args.Length < 1)
            {
                return -1;
            }
            string processNameContain = args[0];
            string keys = args.Length>1 ? args[1] : "";
            string result = sendKey(processNameContain, keys);
            if ("ok".Equals(result))
            {
                Console.WriteLine("0");
                return 0;
            }
            Console.WriteLine("-1");
            return -1;
        }

        static string sendKey(string processNameContain, string keys)
        {
            Process[] processes = Process.GetProcesses();
            for (int i = 0; i < processes.Length; i++)
            {
                Process process = processes[i];
                string name = process.ProcessName;
                if (name.ToLower().Contains(processNameContain))
                {
                    int id = process.Id;
                    return sendKeys(keys, id);
                }
            }
            return "no process found";
        }

        public static string sendKeys(string keysToSend, int pid)
        {
            int wait = 0;
            Process process = null;
            try
            {
                process = Process.GetProcessById(pid);
            }
            catch (Exception ex)
            {
                WriteError(ex.ToString());
                return "error";
            }

            if (process.MainWindowHandle == IntPtr.Zero)
            {
                WriteError($"Process {process.ProcessName} ({process.Id}) has no main window handle.");
                return "error";
            }
            else
            {
                if (wait > 0)
                    Thread.Sleep(wait);
                SetForegroundWindow(process.MainWindowHandle);
                if (keysToSend.Length > 0) {
                    System.Windows.Forms.SendKeys.SendWait(keysToSend);
                }
                return "ok";
                //SendKeys.exe -pid:10384 "%{f}"  alt+f
                //SendKeys.exe - pid:10384 "format C:{Enter}"
                //SendKeys.exe -pid:7184 "private String test{Enter}"
            }
        }

        public static void SetText(string content)
        {
            Clipboard.SetText(content);
        }
        public static void paste(string processNameContain)
        {
            sendKey(processNameContain, "^{v}");
        }

        private static void WriteError(string message)
        {
            AttachConsole(ATTACH_PARENT_PROCESS);
            Console.WriteLine(message);
            FreeConsole();
        }

    }

}
