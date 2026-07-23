import http.server
import socketserver
import webbrowser
import os
import sys

PORT = 8080
DIRECTORY = os.path.dirname(os.path.abspath(__file__))

class Handler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)

def start_server():
    os.chdir(DIRECTORY)
    with socketserver.TCPServer(("", PORT), Handler) as httpd:
        url = f"http://localhost:{PORT}"
        print("=" * 70)
        print("      BHARAT INVEST OS - HEDGE FUND WEB APPLICATION SERVING     ")
        print("=" * 70)
        print(f"  [OK] Local Web Server Running at: {url}")
        print(f"  [OK] Opening web application in your default web browser...")
        print("  Press Ctrl+C in terminal to stop the server.\n")
        
        webbrowser.open(url)
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print("\nShutting down web server cleanly. Goodbye!")
            sys.exit(0)

if __name__ == "__main__":
    start_server()
