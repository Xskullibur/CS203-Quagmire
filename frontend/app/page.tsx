// pages/index.jsx
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";

export default function Home() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-background via-muted to-muted/20 text-foreground">
      <main className="pt-20 p-8 flex flex-col items-center align-center">
        <section className="text-center h-[85vh] content-center">
          <h1 className="text-6xl font-bold mb-4 bg-clip-text text-transparent bg-gradient-to-r from-primary to-muted-foreground">Hand Hathaway <span className="text-black">💪</span></h1>
          <p className="text-xl text-muted-foreground mb-8">Break a leg!</p>
          <Button size="lg" className="bg-primary text-primary-foreground hover:bg-primary/90 transition mr-8">Solo Queue</Button>
          <Button size="lg" className="bg-primary text-primary-foreground hover:bg-primary/90 transition">Tournament</Button>
        </section>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <Card className="col-span-1 md:col-span-2 lg:col-span-2 bg-gradient-to-br from-card/50 to-card/30 backdrop-blur-sm border-none shadow-lg">
            <CardHeader>
              <CardTitle className="text-2xl">Event Highlights</CardTitle>
              <CardDescription>What makes our tournament special</CardDescription>
            </CardHeader>
            <CardContent>
              <ul className="list-disc list-inside space-y-2">
                <li>International competitors from over 30 countries</li>
                <li>Live streaming of all matches</li>
                <li>Interactive fan experience with real-time voting</li>
                <li>Professional referees and state-of-the-art equipment</li>
              </ul>
            </CardContent>
          </Card>

          <Card className="bg-gradient-to-br from-card/50 to-card/30 backdrop-blur-sm border-none shadow-lg">
            <CardHeader>
              <CardTitle className="text-2xl">Prize Pool</CardTitle>
              <CardDescription>Compete for glory and rewards</CardDescription>
            </CardHeader>
            <CardContent>
              <p className="text-4xl font-bold text-primary mb-2">$50,000</p>
              <p>Distributed among top performers across all categories</p>
            </CardContent>
          </Card>

          <Card className="bg-gradient-to-br from-card/50 to-card/30 backdrop-blur-sm border-none shadow-lg">
            <CardHeader>
              <CardTitle className="text-2xl">Venue</CardTitle>
              <CardDescription>Where the action happens</CardDescription>
            </CardHeader>
            <CardContent>
              <p className="font-semibold text-lg">Sports Arena, Downtown</p>
              <p>State-of-the-art facility with capacity for 5000 spectators</p>
            </CardContent>
          </Card>

          <Card className="bg-gradient-to-br from-card/50 to-card/30 backdrop-blur-sm border-none shadow-lg">
            <CardHeader>
              <CardTitle className="text-2xl">Schedule</CardTitle>
              <CardDescription>Mark your calendars</CardDescription>
            </CardHeader>
            <CardContent>
              <p><span className="font-semibold">Qualifiers:</span> Sept 10-12, 2024</p>
              <p><span className="font-semibold">Main Event:</span> Sept 15-17, 2024</p>
            </CardContent>
          </Card>

          <Card className="col-span-1 md:col-span-2 lg:col-span-1 bg-gradient-to-br from-card/50 to-card/30 backdrop-blur-sm border-none shadow-lg">
            <CardHeader>
              <CardTitle className="text-2xl">Live Streaming</CardTitle>
              <CardDescription>Watch from anywhere</CardDescription>
            </CardHeader>
            <CardContent>
              <p className="mb-4">Stream all matches live on our official website and mobile app</p>
              <Button variant="outline" className="hover:bg-primary hover:text-primary-foreground transition">Learn More</Button>
            </CardContent>
          </Card>

          <Card className="col-span-1 md:col-span-3 bg-gradient-to-br from-card/50 to-card/30 backdrop-blur-sm border-none shadow-lg">
            <CardHeader>
              <CardTitle className="text-2xl">Featured Athletes</CardTitle>
              <CardDescription>Meet the stars of arm wrestling</CardDescription>
            </CardHeader>
            <CardContent className="flex justify-around">
              {['John Doe', 'Jane Smith', 'Mike Johnson'].map((name) => (
                <div key={name} className="text-center">
                  <div className="w-20 h-20 bg-primary/20 rounded-full mb-2 mx-auto"></div>
                  <p className="font-semibold">{name}</p>
                </div>
              ))}
            </CardContent>
          </Card>
        </div>
      </main>
    </div>
  );
}