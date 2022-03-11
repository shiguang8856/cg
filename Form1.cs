using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Net;
using System.Speech.Synthesis;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;



namespace WindowsFormsApp1
{
    public partial class Form1 : Form
    {
        SpeechSynthesizer speechSynthesizerObj = new SpeechSynthesizer();
        public Form1()
        {
            InitializeComponent();
            //this.WindowState = FormWindowState.Maximized;
            //this.MinimumSize = this.Size;
            //this.MaximumSize = this.Size;

            //speechSynthesizerObj = new SpeechSynthesizer();
        }

        private void richTextBox1_TextChanged(object sender, EventArgs e)
        {
            
        }
        bool speakStarted = false;
        void synthesizer_SpeakStarted(object sender, System.Speech.Synthesis.SpeakStartedEventArgs e)
        {
            //richTextBox1.SelectionColor = Color.Black;
            //richTextBox1.Select(0, richTextBox1.Text.Length);
            richTextBox1.SelectionStart = 0;
            //richTextBox1.SelectionLength = 0;
            richTextBox1.ScrollToCaret();
        }
        void synthesizer_SpeakProgress(object sender, System.Speech.Synthesis.SpeakProgressEventArgs e)
        {
            //To Write each word and its character postion to the console.
            //this.richTextBox1.Focus();
            //string[] lines = this.richTextBox1.Lines;
            //for(int i= currentReadingLineNum; i <= lines.Length; i++)
            //{
            //    int readingTextIndex = lines[i].IndexOf(e.Text, startIndex);
            //    startIndex = readingTextIndex + 1;
            //}
            //if (lastSelectWordStartPosition != 0) {
            //    this.richTextBox1.SelectionBackColor = Color.White;
            //    this.richTextBox1.Select(lastSelectWordStartPosition, lastSelectWordCharCount);
            //}
            //
            //richTextBox1.SelectionStart = 0;
            //richTextBox1.SelectionFont = new Font("Verdana", 10, FontStyle.Regular);
            //richTextBox1.SelectionLength = 0;
            //richTextBox1.SelectionStart = 0;
            this.richTextBox1.SelectionColor = Color.Gray;
            //this.richTextBox1.SelectionBackColor = Color.GreenYellow;
            
            //lastSelectWordStartPosition = e.CharacterPosition;
            //lastSelectWordCharCount = e.CharacterCount;
            //textBox1.Text = e.Text;
            richTextBox1.SelectionStart = e.CharacterPosition;
            this.richTextBox1.Select(e.CharacterPosition, e.CharacterCount);
            //richTextBox1.SelectedText = e.Text;
            richTextBox1.ScrollToCaret();
            //if(e.CharacterPosition == richTextBox1.Text.Length)
            //this.richTextBox1.SelectedText = e.Text;
            //Console.WriteLine("CharPos: {0}   CharCount: {1}   AudioPos: {2}    \"{3}\"", e.CharacterPosition, e.CharacterCount, e.AudioPosition, e.Text);
        }
        void synthesizer_SpeakComplete(object sender, System.Speech.Synthesis.SpeakCompletedEventArgs e)
        {
            //richTextBox1.SelectionColor = Color.Black;
            //richTextBox1.Select(0, richTextBox1.Text.Length);
            richTextBox1.SelectionStart = 0;
            //richTextBox1.SelectionLength = 0;
            richTextBox1.ScrollToCaret();

            if (checkBox1.Checked)
            {
                //Thread.Sleep(3000);
                //startSpeak();
            }
        }
        private void button1_Click(object sender, EventArgs e)
        {

            //speechSynthesizerObj.Speak(richTextBox1.Text);
            startSpeak();
        }

        private void startSpeak()
        {
            
            foreach (InstalledVoice voice in speechSynthesizerObj.GetInstalledVoices())
            {
                VoiceInfo info = voice.VoiceInfo;
                Console.WriteLine(" Voice Name: " + info.Name);
                //speechSynthesizerObj.SelectVoice(info.Name);
            }
            richTextBox1.SelectionStart = 0;
            richTextBox1.ScrollToCaret();
            //this.richTextBox1.SelectionBackColor = Color.White;
            //this.richTextBox1.Select(0, this.richTextBox1.Text.Length);

            speechSynthesizerObj.SpeakStarted += new EventHandler<System.Speech.Synthesis.SpeakStartedEventArgs>(synthesizer_SpeakStarted);
            speechSynthesizerObj.SpeakProgress += new EventHandler<System.Speech.Synthesis.SpeakProgressEventArgs>(synthesizer_SpeakProgress);
            speechSynthesizerObj.SpeakCompleted += new EventHandler<System.Speech.Synthesis.SpeakCompletedEventArgs>(synthesizer_SpeakComplete);

            //speechSynthesizerObj.GetCurrentlySpokenPrompt();
            //speechSynthesizerObj.SelectVoice("en-US");
            //CultureInfo.DefaultThreadCurrentCulture = new CultureInfo("en-US");
            //CultureInfo enUs = new CultureInfo("en-US");
            VoiceGender gender;
            string text = comboBox2.Text;
            if ("Female".Equals(text))
            {
                gender = VoiceGender.Female;
            }
            else
            {
                gender = VoiceGender.Male;
            }
            CultureInfo cultureInfo;
            //string text = comboBox3.Text;
            if ("zh-CN".Equals(comboBox3.Text))
            {
                cultureInfo = new CultureInfo("zh-CN");
            }
            else
            {
                cultureInfo = new CultureInfo("en-US");
            }
            speechSynthesizerObj.SelectVoiceByHints(gender, VoiceAge.Adult, 1, cultureInfo);

            speechSynthesizerObj.SpeakAsync(richTextBox1.Text);
        }

        private void openFileDialog1_FileOk(object sender, CancelEventArgs e)
        {

        }

        private void browse_Click(object sender, EventArgs e)
        {
            DialogResult dr = openFileDialog1.ShowDialog();
            if (dr == System.Windows.Forms.DialogResult.OK)
            {
                fileName.Text = openFileDialog1.FileName;

            }
        }

        string GET(string url)
        {
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            try
            {
                WebResponse response = request.GetResponse();
                using (Stream responseStream = response.GetResponseStream())
                {
                    StreamReader reader = new StreamReader(responseStream, System.Text.Encoding.UTF8);
                    string json = reader.ReadToEnd();

                    return reader.ReadToEnd();
                }
            }
            catch (WebException ex)
            {
                WebResponse errorResponse = ex.Response;
                using (Stream responseStream = errorResponse.GetResponseStream())
                {
                    StreamReader reader = new StreamReader(responseStream, System.Text.Encoding.GetEncoding("utf-8"));
                    String errorText = reader.ReadToEnd();
                    // log errorText
                }
                throw;
            }
        }

        void readJson(string json)
        {
            //Object jsonObj = JObject.Parse(stringResult);

            //JToken pathResult = jsonObj.SelectToken("results[0].example");

            //return pathResult.ToString();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            //this.richTextBox1.SelectionBackColor = Color.White;
            //this.richTextBox1.Select(0, this.richTextBox1.Text.Length);
            richTextBox1.SelectionLength = 0;
            richTextBox1.SelectionStart = 0;
            richTextBox1.ScrollToCaret();
            speechSynthesizerObj.SpeakAsyncCancelAll();
        }

        private void button3_Click(object sender, EventArgs e)
        {
            speechSynthesizerObj.Pause();
        }

        private void button4_Click(object sender, EventArgs e)
        {
            speechSynthesizerObj.Resume();
        }

        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
            
        }
    }
}
