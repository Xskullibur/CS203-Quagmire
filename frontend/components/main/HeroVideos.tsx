import HoverVideoGuide from './HoverVideoGuide';

const signUpSteps = [
    {
        title: "Find a Tournament",
        description: "Search for upcoming arm wrestling tournaments in your area or online.",
        videoUrl: "/videos/buffswim.mp4"
    },
    {
        title: "Check Eligibility",
        description: "Review the tournament rules and ensure you meet all requirements.",
        videoUrl: "/videos/buffsuit.mp4"
    },
    {
        title: "Register Online",
        description: "Complete the online registration form and pay the entry fee.",
        videoUrl: "/videos/sweatycode.mp4"
    },
    {
        title: "Prepare for Weigh-In",
        description: "Get ready for the official weigh-in before the tournament.",
        videoUrl: "/videos/jjk.mp4"
    },
    {
        title: "Attend the Event",
        description: "Arrive at the venue on time and participate in the tournament.",
        videoUrl: "/videos/idwin.mp4"
    }
];

export default function HeroVideos() {
    return (
        <div className='w-full py-12'>
            <HoverVideoGuide steps={signUpSteps} />
        </div>
    );
}