from flask import Flask, send_file, request, abort
from pathlib import Path
import youtube_dl
import json

app = Flask(__name__)

@app.route('/queuemp3', methods=['GET', 'POST'])
def queuemp3():
    if request.method == 'POST':
        try:
            data = request.get_json()
            url = data['url']
            print(url)

            ydl = youtube_dl.YoutubeDL()
            r = None
            with ydl:
                # don't download, much faster
                r = ydl.extract_info(url, download=False)

            options = {
                'format': 'bestaudio/best',
                'extractaudio': True,  # only keep the audio
                'audioformat': "mp3",  # convert to mp3
                'outtmpl': '{}.mp3'.format(r['title']),  # name the file the ID of the video
                'noplaylist': True,  # only download single song, not playlist
            }

            ''' print some typical fields if needed
            print("%s uploaded by '%s', has %d views, %d likes, and %d dislikes" % (
                r['title'], r['uploader'], r['view_count'], r['like_count'], r['dislike_count']))'''

            with youtube_dl.YoutubeDL(options) as ydl:
                ydl.download([url])
            try:
                return json.dumps({'filename': r['title']})
            except Exception as e:
                return str(e)
        finally:
            print("A request was sent for queueing a conversion")


@app.route('/downloadmp3', methods=['GET', 'POST'])
def downloadmp3():
    if request.method == 'POST':
        filename = request.form['filename']
        print(filename)
        audio_file = Path("./{}.mp3".format(filename))
        if audio_file.is_file():
            return send_file('./{}.mp3'.format(filename),
                             attachment_filename='{}.mp3'.format(filename))
        else:
            abort(404)

if __name__ ==  "__main__":
    app.run(host="0.0.0.0", port=8080, debug=True)